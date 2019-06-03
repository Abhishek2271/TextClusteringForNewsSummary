package Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/*
    <Created Date> Nov 10, 2018 </CreatedDate>
    <LastUpdated> Nov 11, 2018 </LastUpdated>
    <About>
        Every computations regarding hash value.
    </About>
*/
public class HashGenerator
{
    String HashType = "MD5";

    /// <summary>
    /// Compute hash from the list of input strings.
    ///</summary>
    /// <param name="InputFields">List of strings from which the hash is to be computed</param>
    /// <Returns>Computed MD5 hash from the given list of fields</Returns>
    public String HashCalculator(List<String> InputFields)
    {
        StringBuffer sb = new StringBuffer();
        try
        {
            StringBuilder BuilderforHash = new StringBuilder();
            for (String Field : InputFields)
            {
                BuilderforHash.append(Field);
            }
            // GET HASH FROM BYTE ARRAY
            byte[] ByteString = BuilderforHash.toString().getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance(HashType);
            byte[] DigestedHash = md.digest(ByteString);

            for (int i = 0; i < DigestedHash.length; ++i)
            {
                sb.append(Integer.toHexString((DigestedHash[i] & 0xFF) | 0x100).substring(1,3));
            }
        }
        catch (UnsupportedEncodingException exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
        catch (NoSuchAlgorithmException exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
        return sb.toString();
    }
}

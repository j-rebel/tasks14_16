import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @CsvBindByName
    public long id;
    @CsvBindByName
    public String firstName;
    @CsvBindByName
    public String lastName;
    @CsvBindByName
    public String country;
    @CsvBindByName
    public int age;
}